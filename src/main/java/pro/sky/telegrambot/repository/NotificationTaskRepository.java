package pro.sky.telegrambot.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    List<NotificationTask> findBySendAtLessThanEqualAndSentFalse(LocalDateTime sendAt);

    @Modifying
    @Transactional
    @Query("""
            update NotificationTask n
            set n.sent = true
            where n.id = :id
            """)
    int markAsSentById(@Param("id") Long id);
}

