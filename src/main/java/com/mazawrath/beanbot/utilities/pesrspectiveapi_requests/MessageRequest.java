package com.mazawrath.beanbot.utilities.pesrspectiveapi_requests;

import au.com.origma.perspectiveapi.v1alpha1.PerspectiveAPI;
import au.com.origma.perspectiveapi.v1alpha1.models.*;

public class MessageRequest {
    String message;
    public static String apiKey;
    PerspectiveAPI pAPI;
    AnalyzeCommentRequest.Builder request;
    AnalyzeCommentResponse response;

    public MessageRequest(String message) {
        this.pAPI = PerspectiveAPI.create(apiKey);
        this.request = new AnalyzeCommentRequest.Builder()
                .comment(new Entry.Builder()
                        .type(ContentType.PLAIN_TEXT)
                        .text(message)
                        .build());
        request.addRequestedAttribute(AttributeType.TOXICITY, null);
        request.addRequestedAttribute(AttributeType.IDENTITY_ATTACK, null);
        request.addRequestedAttribute(AttributeType.INSULT, null);
        request.addRequestedAttribute(AttributeType.THREAT, null);
        request.addRequestedAttribute(AttributeType.INCOHERENT, null);
        request.addRequestedAttribute(AttributeType.SEXUALLY_EXPLICIT, null);

        requestAnalysis();
    }

    private void requestAnalysis() {
        response = pAPI.analyze(request.build());
    }

    public float getToxicityProb() {
        return response.getAttributeScore(AttributeType.TOXICITY).getSummaryScore().getValue();
    }

    public float getIdentityAttackProb() {
        return response.getAttributeScore(AttributeType.IDENTITY_ATTACK).getSummaryScore().getValue();
    }

    public float getInsultProb() {
        return response.getAttributeScore(AttributeType.INSULT).getSummaryScore().getValue();
    }

    public float getIncoherentProb() {
        return response.getAttributeScore(AttributeType.INCOHERENT).getSummaryScore().getValue();
    }

    public float getSexuallyExplicitProb() {
        return response.getAttributeScore(AttributeType.SEXUALLY_EXPLICIT).getSummaryScore().getValue();
    }
}
