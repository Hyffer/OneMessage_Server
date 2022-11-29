package xyz.hyffer.onemessage_server.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.hyffer.onemessage_server.storage.component.Source;

import java.util.List;

@Mapper
public interface SourceMapper {

    List<Source> getSources();

    /**
     * Add a new source, with UNIQUE name
     * @param source source to be added,
     *               auto increment key `id` will be assigned
     * @return affected rows
     */
    Integer addSource(Source source);

}
