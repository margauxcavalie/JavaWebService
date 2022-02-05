package fr.epita.assistant.jws.data.model;
import fr.epita.assistant.jws.domain.entity.MapEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "game_map")
@AllArgsConstructor @NoArgsConstructor @With
public class MapModel
{
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public String map;
    @ManyToOne public GameModel game;

}
