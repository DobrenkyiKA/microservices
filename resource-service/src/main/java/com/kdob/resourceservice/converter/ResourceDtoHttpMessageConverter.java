package com.kdob.resourceservice.converter;

import com.kdob.resourceservice.dto.CreateResourceRequestDto;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStream;

public class ResourceDtoHttpMessageConverter extends AbstractHttpMessageConverter<CreateResourceRequestDto> {

    public ResourceDtoHttpMessageConverter() {
        super(MediaType.valueOf("audio/mpeg"));
    }

    @Override
    protected boolean supports(final @NonNull Class<?> clazz) {
        return CreateResourceRequestDto.class.isAssignableFrom(clazz);
    }

    @Override
    protected CreateResourceRequestDto readInternal(final @NonNull Class<? extends CreateResourceRequestDto> clazz,
                                                                final @NonNull HttpInputMessage inputMessage) throws IOException {
        InputStream inputStream = inputMessage.getBody();
        byte[] bytes = inputStream.readAllBytes();

        CreateResourceRequestDto dto = new CreateResourceRequestDto();
        dto.setResource(bytes);
        return dto;
    }

    @Override
    protected void writeInternal(final @NonNull CreateResourceRequestDto dto, final @NonNull HttpOutputMessage outputMessage) {
        // Not required if you only use this to read request
    }
}