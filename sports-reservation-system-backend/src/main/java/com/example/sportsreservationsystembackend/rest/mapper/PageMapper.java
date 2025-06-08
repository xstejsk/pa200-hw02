package com.example.sportsreservationsystembackend.rest.mapper;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.xstejsk.reservationapp.main.rest.model.UsersPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * This class represents page mapper
 *
 * @author Radim Stejskal
 */

@Component
@RequiredArgsConstructor
public class PageMapper {

    private final AppUserMapper usersMapper;

    private <T> T createDtoPage(Page<?> page, Class<T> dtoPageClass) {
        T dtoPage;
        try {
            dtoPage = dtoPageClass.newInstance();
            BeanUtils.copyProperties(page, dtoPage);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error creating DTO page instance", e);
        }
        return dtoPage;
    }

    private <T> void setContent(Object dtoPage, List<T> content) {
        try {
            Method setContentMethod = dtoPage.getClass().getMethod("setContent", List.class);
            setContentMethod.invoke(dtoPage, content);
        } catch (Exception e) {
            throw new RuntimeException("Error setting 'records' property", e);
        }
    }



    /**
     * This method is used for mapping page of users to users page DTO
     * @param page
     * @return users page DTO
     */
    public UsersPage toUsersPage(Page<AppUser> page) {
        UsersPage usersPage = createDtoPage(page, UsersPage.class);
        setContent(usersPage, page.getContent().stream().map(usersMapper::appUserToAppUserDTO).toList());
        return usersPage;
    }


}