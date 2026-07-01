package ru.akozadaev.portal.application.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, UUID> {

	List<ApplicationEntity> findAllByStatusOrderByCreatedAtAsc(ApplicationStatus status);

	@Query(
			value = """
					SELECT *
					FROM applications
					WHERE status = :status
					ORDER BY created_at
					LIMIT :limit
					FOR UPDATE SKIP LOCKED
					""",
			nativeQuery = true)
	List<ApplicationEntity> findBatchForProcessing(@Param("status") String status, @Param("limit") int limit);
}
