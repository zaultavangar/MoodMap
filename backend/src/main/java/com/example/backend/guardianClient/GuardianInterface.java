package com.example.backend.guardianClient;

import the.guardian.api.http.content.ContentResponse;

public interface GuardianInterface {
    public ContentResponse fetchByContent(String fromDate, String toDate, int page, int pageSize);
}
