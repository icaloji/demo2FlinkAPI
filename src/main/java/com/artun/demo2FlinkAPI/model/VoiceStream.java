package com.artun.demo2FlinkAPI.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * Created by I.C.ARTUN
 */

@Data
public class VoiceStream implements Serializable {

    private String id;
    private String phoneNumber;
    private Long duration;
    private Date callStartDate;
    private Boolean active;

    public VoiceStream(String phoneNumber, Long duration, Date callStartDate, Boolean active) {
        this.phoneNumber = phoneNumber;
        this.duration = duration;
        this.callStartDate = callStartDate;
        this.active = active;
    }
}

