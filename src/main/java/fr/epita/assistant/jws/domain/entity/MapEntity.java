package fr.epita.assistant.jws.domain.entity;
import fr.epita.assistant.jws.data.model.MapModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@With @NoArgsConstructor @AllArgsConstructor
public class MapEntity
{
    public List<Long> ids;
    public List<String> map;

    public static MapEntity ModelToEntity(List<MapModel> mapModels)
    {
        List<Long> ids = new ArrayList<>();
        List<String> map = new ArrayList<>();

        mapModels.forEach(mapModel -> {
            ids.add(mapModel.id);
            map.add(mapModel.map);
        });

        for (int i = 0; i < map.size() - 1; i++)
        {
            if (ids.get(i) > ids.get(i + 1))
            {
                for (int j = 0; j < map.size() - 1; j++)
                {
                    if (ids.get(i) < ids.get(j))
                    {
                        Long temp = ids.get(i);
                        ids.remove(i);
                        ids.add(i, ids.get(j));
                        ids.remove(j);
                        ids.add(j, temp);
                        //
                        String temp2 = map.get(i);
                        map.remove(i);
                        map.add(i, map.get(j));
                        map.remove(j);
                        map.add(j, temp2);
                    }
                }
            }
        }

        return new MapEntity().withMap(map).withIds(ids);
    }

    public static MapEntity MapFromPath (String fileName)
    {
        List<String> map = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                map.add(line);
                ids.add(null);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new MapEntity().withMap(map).withIds(ids);
    }
}
