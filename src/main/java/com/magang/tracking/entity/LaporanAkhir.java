package com.magang.tracking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "laporan_akhir")
public class LaporanAkhir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relasi Many-to-One ke tabel mahasiswa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mahasiswa_id", nullable = false)
    private Mahasiswa mahasiswa;

    @Column(name = "file_laporan", nullable = false)
    private String fileLaporan; // Menyimpan path lokasi file PDF

    @Column(name = "tanggal_upload", nullable = false)
    private LocalDateTime tanggalUpload;

    @PrePersist
    protected void onCreate() {
        this.tanggalUpload = LocalDateTime.now();
    }
}
