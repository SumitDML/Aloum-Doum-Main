package com.aloumDaum.user.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "device_details")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "token")
    private String token;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "user_id")
    private String userId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "login_device_id", referencedColumnName = "id")
    private LoginEntity loginEntity;


}
