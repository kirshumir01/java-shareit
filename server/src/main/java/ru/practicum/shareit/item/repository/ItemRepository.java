package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(value = "item-entity-graph")
    List<Item> findAllByOwnerIdOrderByIdAsc(long ownerId);

    @Query("""
            SELECT it FROM Item AS it
            WHERE(lower(it.name) like '%'||lower(:text)||'%' OR
            lower(it.description) like '%'||lower(:text)||'%')
            AND it.available = true
            """)
    @EntityGraph(value = "item-entity-graph")
    List<Item> getByText(@Param("text") String text);

    @Query("""
            SELECT it FROM Item AS it
            WHERE(it.request.id) IN :requestIds
            """)
    @EntityGraph(value = "item-entity-graph")
    List<Item> findByRequestId(List<Long> requestIds);

    @Query("""
            SELECT it FROM Item AS it
            WHERE it.request.id = :requestId
            """)
    @EntityGraph(value = "item-entity-graph")
    List<Item> findByRequestId(long requestId);

}