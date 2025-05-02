package com.example.YouTubeDL;

import java.time.LocalDate;

public interface LastUpdateRepository {
    public int setDate(LocalDate date);

    public boolean canUpdate();
}
