package me.zink.clicker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "action_list")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EAction action;

    @Getter
    @Setter
    @Column(length = 24)
    private long serverTimestamp;

    @Getter
    @Setter
    @Column(length = 24)
    private long clientTimestamp;

    @Getter
    @Setter
    @Column(length = 24)
    private String info;

    @Getter
    @Setter
    @Column(length = 3)
    private int location;

    public Action() {

    }

    public Action(Action action, long clientTimestamp, long serverTimestamp) {
        this.action = action.getAction();
        this.location = action.getLocation();
        this.info = action.getInfo();

        this.clientTimestamp = clientTimestamp;
        this.serverTimestamp = serverTimestamp;
    }

    public Action(EAction action, String info, int location, long serverTimestamp, long clientTimestamp) {
        this.action = action;
        this.info = info;
        this.location = location;
        this.serverTimestamp = serverTimestamp;
        this.clientTimestamp = clientTimestamp;
    }
}
