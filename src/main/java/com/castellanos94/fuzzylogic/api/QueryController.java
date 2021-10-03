package com.castellanos94.fuzzylogic.api;

import com.castellanos94.fuzzylogic.api.model.EvaluationQuery;
import com.castellanos94.fuzzylogic.api.model.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
public class QueryController {
    @RequestMapping(value = "/evaluation", method = RequestMethod.POST, consumes = { "multipart/form-data" })
    public ResponseEntity<ResponseModel> upload(@RequestPart("query") @Valid EvaluationQuery evaluationQuery,
                                                @RequestPart("dataset") @Valid @NotNull @NotBlank MultipartFile file) {
        StringBuilder msgBuilder = new StringBuilder();
        evaluationQuery.getStates().stream().filter(s-> s.getF()==null).forEachOrdered(linguisticState -> msgBuilder.append(linguisticState.getLabel()).append(", "));
        ResponseModel responseModel = new ResponseModel();
        if(msgBuilder.length() >0){
            responseModel.setStatus(ResponseModel.Status.Failed);
            String msg = msgBuilder.toString();
            msg = msg.substring(0,msg.lastIndexOf(","));
            responseModel.setMsg("The following linguistic states have no membership function: "+msg);
            return ResponseEntity.badRequest().body(responseModel);
        }
        System.out.println(evaluationQuery);
        FileUploadController.printFileDetails(file);
        responseModel.setStatus(ResponseModel.Status.Created);
        responseModel.setId("1213214A");
        return  ResponseEntity.ok(responseModel);
    }
}
