package datn.category_service.models.entities.bases;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import datn.category_service.models.entities.enums.TypeCategory;

@Data
@Table(name = "categories")
@Builder
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class CategoryEntity extends BaseEntity{
    @Column(name = "title_category", nullable = false)
    private String title;

    @Column(name = "icon_url", nullable = false)
    private String icon;

    @Column(name = "color", nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_category")
    private TypeCategory type;
}
