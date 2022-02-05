package fr.epita.assistant.jws.data.repository;
import fr.epita.assistant.jws.data.model.MapModel;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MapRepository implements PanacheRepositoryBase<MapModel, Long> {
}
