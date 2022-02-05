package fr.epita.assistant.jws.domain.entity;
import fr.epita.assistant.jws.data.model.MapModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        MapEntity mapEntity = new MapEntity()
                .withMap(map)
                .withIds(ids);

        return mapEntity;
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
