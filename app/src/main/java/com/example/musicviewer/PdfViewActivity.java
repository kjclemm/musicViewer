package com.example.musicviewer;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;

public class PdfViewActivity extends AppCompatActivity {

    private ImageView pdfImageView;
    private Button prevPageBtn, nextPageBtn;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private int currentPageIndex = 0;
    private File localFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        pdfImageView = findViewById(R.id.pdfImageView);
        prevPageBtn = findViewById(R.id.prevPageBtn);
        nextPageBtn = findViewById(R.id.nextPageBtn);

        String pdfUrl = getIntent().getStringExtra("pdfUrl");
        downloadPdfFile(pdfUrl);

        prevPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageIndex > 0) {
                    showPage(--currentPageIndex);
                }
            }
        });

        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageIndex < pdfRenderer.getPageCount() - 1) {
                    showPage(++currentPageIndex);
                }
            }
        });
    }

    private void downloadPdfFile(String pdfUrl) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        try {
            localFile = File.createTempFile("pdf", "pdf");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    try {
                        openPdfRenderer(Uri.fromFile(localFile));
                        showPage(currentPageIndex);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@Nullable Exception exception) {
                    Toast.makeText(PdfViewActivity.this, "Failed to download file", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPdfRenderer(Uri uri) throws IOException {
        ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        if (fileDescriptor != null) {
            pdfRenderer = new PdfRenderer(fileDescriptor);
        }
    }

    private void showPage(int index) {
        if (pdfRenderer != null && index >= 0 && index < pdfRenderer.getPageCount()) {
            if (currentPage != null) {
                currentPage.close();
            }
            currentPage = pdfRenderer.openPage(index);

            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            pdfImageView.setImageBitmap(bitmap);
            updateButtons();
        }
    }

    private void updateButtons() {
        prevPageBtn.setEnabled(currentPageIndex > 0);
        nextPageBtn.setEnabled(currentPageIndex < pdfRenderer.getPageCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentPage != null) {
            currentPage.close();
        }
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
    }
}
