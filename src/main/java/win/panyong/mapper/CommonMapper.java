package win.panyong.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("commonMapper")
public interface CommonMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO ${table} ( ${columns} ) VALUES ( ${values} )")
    void executeInsertSqlQuery(InsertParameter parameter);

    @Delete("${sql}")
    void executeDeleteSqlQuery(@Param("sql") String sql);

    @Update("${sql}")
    void executeUpdateSqlQuery(@Param("sql") String sql);

    @Select("${sql}")
    List<Map> executeSelectSqlQuery(Map<String, Object> parameterMap);

}
