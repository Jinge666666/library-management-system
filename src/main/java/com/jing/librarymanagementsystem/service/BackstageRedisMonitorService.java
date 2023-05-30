package com.jing.librarymanagementsystem.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;

@Service
public interface BackstageRedisMonitorService {

    public Map<String, Properties>  getRedisServerInfo();
}
