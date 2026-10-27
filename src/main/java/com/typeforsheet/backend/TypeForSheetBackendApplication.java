package com.typeforsheet.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TypeForSheetBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(TypeForSheetBackendApplication.class, args);
  }
}
