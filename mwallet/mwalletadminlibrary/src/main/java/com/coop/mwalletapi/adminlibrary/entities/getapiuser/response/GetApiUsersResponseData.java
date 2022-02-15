package com.coop.mwalletapi.adminlibrary.entities.getapiuser.response;
import com.coop.mwalletapi.adminlibrary.dao.model.GetApiUserDataDb;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author okahia
 */

@Getter
@Setter
public class GetApiUsersResponseData {
    List<GetApiUserDataDb> apiUsers;
}
