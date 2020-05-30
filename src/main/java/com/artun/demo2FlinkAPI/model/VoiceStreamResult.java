package com.artun.demo2FlinkAPI.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *
 * Created by I.C.ARTUN
 */

// Lombok kutuphanesini kullanarak olusturuyoruz
@Data
@EqualsAndHashCode(of = {"id"}) // equals ve hashCode motodlarini olustururken id field'ini kullanarak olusturuyoruz.
@NoArgsConstructor // hicbir parametrenin olmadigi constructor olusturuyoruz
@AllArgsConstructor // tum parametrenin olmadigi constructor olusturuyoruz
@Table(value = "voice_stream_result") // cassandra db de tablo olusturuyoruz
public class VoiceStreamResult implements Serializable {

    @PrimaryKey
    private String id = UUID.randomUUID().toString(); // cassandra da time based UUID dir. Bu nesne olusturuldugunda UUID nin random hali bu nesneye esit olsun.

    @Column(value = "phone_number")
    private String phoneNumber;

    @Column(value = "duration")
    private Long duration;

    @Column(value = "call_start_date")
    private Date callStartDate;

    @Column(value = "created_at")
    private Date createdAt;
}
