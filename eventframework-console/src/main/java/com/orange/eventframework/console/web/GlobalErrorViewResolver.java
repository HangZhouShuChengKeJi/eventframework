package com.orange.eventframework.console.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 全局异常处理类
 *
 * @author 小天
 * @date 2019/1/21 16:48
 */
@Component
public class GlobalErrorViewResolver implements ErrorViewResolver {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        ModelAndView mav = new ModelAndView();

        mav.addAllObjects(model);

        Exception ex = (Exception) request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);

        if (ex == null) {
            logger.error(MessageFormatter.arrayFormat("path: {}, code: {}, message: {}",
                    new Object[]{model.get("path"), model.get("status"), model.get("message")}).getMessage());
        } else {
            logger.error(MessageFormatter.arrayFormat("path: {}, code: {}, message: {}",
                    new Object[]{model.get("path"), model.get("status"), model.get("message")}).getMessage(), ex);
        }

        if (status == HttpStatus.NOT_FOUND) {
            mav.setViewName("error/404");
        } else if (status.is4xxClientError()) {
            mav.setViewName("error/4xx");
        } else if (status.is5xxServerError()) {
            mav.setViewName("error/5xx");
        } else {
            mav.setViewName("error/error");
        }
        return mav;
    }
}
