package com.wizz.hospitalSell.dao;

import com.wizz.hospitalSell.domain.AdminInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AdminInfoDao extends JpaRepository<AdminInfo, Integer> {

    Boolean existsByAdminNameAndAdminPass(String adminName, String adminPass);

    Boolean existsByAdminName(String adminName);

}
