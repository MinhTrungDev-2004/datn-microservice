package datn.category_service.repositories;

import datn.category_service.models.entities.bases.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
}
