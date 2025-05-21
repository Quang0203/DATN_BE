package datn.datnbe.Mapper;


import datn.datnbe.Entity.User;
import datn.datnbe.dto.request.UserCreationRequest;
import datn.datnbe.dto.response.UserRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public User toUser(UserCreationRequest request);
    public User updateUser(@MappingTarget User user, UserCreationRequest request);
    public UserRespone toUserRespone(User user);
}
