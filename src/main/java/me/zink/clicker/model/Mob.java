package me.zink.clicker.model;

import jakarta.persistence.*;
import lombok.Data;
import me.zink.clicker.util.MobUtils;

@Data
@Entity
@Table(name = "mobs")
public class Mob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MobUtils.MobType type;

    public Mob(MobUtils.MobType type) {
        this.type = type;
    }

    public Mob(){}
}
