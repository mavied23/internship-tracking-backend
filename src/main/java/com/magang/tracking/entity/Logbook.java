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
@Table(name = "logbook")
public class Logbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mahasiswa_id", nullable = false)
    private Mahasiswa mahasiswa;

    @Column(nullable = false)
    private LocalDate tanggal;

    @Column(name = "jam_kerja", nullable = false)
    private Integer jamKerja;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String aktivitas;

    private String dokumentasi;

    @Column(nullable = false)
    private String status;

    @Column(name = "catatan_dosen", columnDefinition = "TEXT")
    private String catatanDosen;
}