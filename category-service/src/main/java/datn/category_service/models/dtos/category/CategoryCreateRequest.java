package datn.category_service.models.dtos.category;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateRequest {
    private String title;
    private String icon;
    private String color;
    private String type;
}
