package com.lessons.services;

import com.lessons.models.MyLookupDTO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

// this tells Spring to instantiate this service as a singleton and make it injectable
@Service
public class MyLookupService {

    private static final Logger logger = LoggerFactory.getLogger(MyLookupService.class);

    @Resource
    private DataSource dataSource;

    public List<MyLookupDTO> getAllPriorities(){

        return new ArrayList<MyLookupDTO>();
    }



}
