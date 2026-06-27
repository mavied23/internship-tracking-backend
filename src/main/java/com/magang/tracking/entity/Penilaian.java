package com.magang.tracking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "penilaian")
@Data // Ini otomatis membuat semua Getter, Setter, Equals, HashCode, dan ToString
@NoArgsConstructor // Membuat constructor kosong
@AllArgsConstructor // Membuat constructor dengan semua field
public class Penilaian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Field-field yang menyebabkan error 'set... undefined'
    private Double disiplin;
    private Double kerjasama;
    private Double kemampuanTeknis;
    private Double komunikasi;
    private Double laporan;
    private Double presentasi;
    private Double monitoring;
    private Double nilaiAkhir;

    // Tambahkan relasi ke Mahasiswa (jika belum ada)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mahasiswa_id", referencedColumnName = "id")
    private Mahasiswa mahasiswa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dosen_id")
    private Dosen dosen;
}