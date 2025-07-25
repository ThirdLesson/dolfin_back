package org.scoula.domain.location.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.location.entity.Location;

@Mapper
public interface

LocationMapper {
    
    void insertLocation(Location location);

    void insertLocationBatch(@Param("locations") List<Location> locations);

    boolean existsByLocationNumber(@Param("locationNumber") String locationNumber);

    List<Location> selectAllCenters();

    List<Location> selectAllBanks();
}
