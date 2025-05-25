package com.example.YouTubeDL.updater;

import java.time.LocalDate;

public interface LastUpdateRepository {
    public int updateDate();

    public boolean canUpdate();
}
