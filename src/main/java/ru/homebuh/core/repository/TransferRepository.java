package ru.homebuh.core.repository;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.TransferEntity;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Long> {
    @Modifying
    @Query("delete TransferEntity tr where tr.userInfo.id in ?1")
    void deleteAllByUserIdIn(Collection<String> userIds);
}
