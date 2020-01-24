package com.blackmorse.hattrick.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Match {
    private Long id;
    private Integer round;
    private Date date;
    private Integer season;
}
