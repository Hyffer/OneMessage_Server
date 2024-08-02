package xyz.hyffer.onemessage_server.client_api.service;

import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.RequestBody;
import xyz.hyffer.onemessage_server.client_api.controller_ws.payload.SendBody;

public interface ClientRequestService {

    SendBody.ResponseBody getContacts(RequestBody.RequestBody_get_contacts requestBody) throws UnexpectedValueException;

    SendBody.ResponseBody getMessages(RequestBody.RequestBody_get_messages requestBody) throws UnexpectedValueException;

    SendBody.ResponseBody updateStatus(RequestBody.RequestBody_update_status requestBody) throws UnexpectedValueException;

    SendBody.ResponseBody postMessage(RequestBody.RequestBody_post_message requestBody) throws UnexpectedValueException;

}
