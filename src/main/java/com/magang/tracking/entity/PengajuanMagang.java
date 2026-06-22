package com.magang.tracking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pengajuan_magang")
public class PengajuanMagang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mahasiswa_id", nullable = false)
    private Mahasiswa mahasiswa;

    @Column(nullable = false)
    private String perusahaan;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String alamat;

    @Column(name = "tanggal_mulai", nullable = false)
    private LocalDate tanggalMulai;

    @Column(name = "tanggal_selesai", nullable = false)
    private LocalDate tanggalSelesai;

    @Column(nullable = false)
    private String dokumen;

    @Column(nullable = false)
    private String status;

    @Column(name = "catatan_admin", columnDefinition = "TEXT")
    private String catatanAdmin;
}