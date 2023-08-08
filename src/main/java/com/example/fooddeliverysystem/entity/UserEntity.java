package com.example.fooddeliverysystem.entity;

import java.io.Serializable;
import java.util.List;

import com.example.fooddeliverysystem.model.UserStatus;

import io.swagger.annotations.ApiModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity implements Serializable {
    @SuppressWarnings(value = "unused")
    private static final Long SerialVersionID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "lastname")
    private String lastName;

    private String email;

    private String password;

    private String token;

    @Column(name = "image_path")
    private String imagePath;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "choosen_one", insertable = false)
    private boolean choosenOne;

    @ManyToOne()
    @JoinColumn(name = "roleid", referencedColumnName = "id")
    private RoleEntity role;

    @OneToMany(mappedBy = "user")
    private List<ConfirmationTokenEntity> confirmationTokens;

    @OneToMany(mappedBy = "user")
    private List<UserOrderEntity> userOrders;

    public UserOrderEntity addUserOrder(UserOrderEntity userOrderEntity) {
        getUserOrders().add(userOrderEntity);
        userOrderEntity.setUser(this);

        return userOrderEntity;
    }

    public boolean removeUserOrder(UserOrderEntity userOrderEntity) {
        for (UserOrderEntity userOrder : userOrders) {
            if (userOrder.getId() == userOrderEntity.getId()) {
                userOrders.remove(userOrder);

                return true;
            }
        }

        return false;
    }
}
