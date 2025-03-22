package cn.nullcat.sckj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.nullcat.sckj.mapper")
public class SckjApplication {

    public static void main(String[] args) {
        SpringApplication.run(SckjApplication.class, args);
    }

}
