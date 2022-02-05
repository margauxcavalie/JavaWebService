package fr.epita.assistant.jws.data.model;
import lombok.*;

import javax.persistence.*;

@Entity @Table(name = "game_map")
@AllArgsConstructor @NoArgsConstructor @With
@Getter @Setter
public class MapModel
{
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public String map;
    @ManyToOne public GameModel game;

    public static String decodeMapRLE(String map)
    {
        final StringBuilder stringBuilder = new StringBuilder();
        char[] characters = map.toCharArray();

        int index = 0;
        int size = characters.length;
        while (index < size)
        {
            int rep = 0;
            while ((index < characters.length) && Character.isDigit(characters[index]))
            {
                rep = rep * 10 + characters[index] - '0';
                index++;
            }

            StringBuilder stringBuilder2 = new StringBuilder();
            while ((index < characters.length) && !Character.isDigit(characters[index]))
            {
                stringBuilder2.append(characters[index]);
                index++;
            }

            if (rep > 0)
                stringBuilder.append(String.valueOf(stringBuilder2).repeat(rep));

            else
                stringBuilder.append(stringBuilder2);
        }

        return stringBuilder.toString();
    }

    public static String encodeMapRLE(String map)
    {
        StringBuilder stringBuilder = new StringBuilder();
        int count;
        for (int index = 0; index < map.length(); index++)
        {
            count = 1;
            while (index < map.length() - 1 && map.charAt(index) == map.charAt(index + 1) && count < 9)
            {
                count++;
                index++;
            }

            stringBuilder.append(count);
            stringBuilder.append(map.charAt(index));
        }

        return stringBuilder.toString();
    }
}
