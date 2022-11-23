package com.aloumDaum.user.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
@Entity
@Table(name = "roles")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RoleEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, length = 50)
    private String roleName;

    @Column(name = "description", columnDefinition = "mediumtext")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "roles_has_roles_permissions", joinColumns = {
            @JoinColumn(name = "roles_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "roles_permissions_id", referencedColumnName = "id")})
    private Set<RolePermissionEntity> permissions;
}
