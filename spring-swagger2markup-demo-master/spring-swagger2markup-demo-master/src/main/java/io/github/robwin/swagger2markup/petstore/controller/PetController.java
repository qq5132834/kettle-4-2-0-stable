/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package io.github.robwin.swagger2markup.petstore.controller;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.github.robwin.swagger2markup.petstore.Responses;
import io.github.robwin.swagger2markup.petstore.model.Pet;
import io.github.robwin.swagger2markup.petstore.model.Pets;
import io.github.robwin.swagger2markup.petstore.repository.MapBackedRepository;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(value = "/pets", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, "application/x-smile"})
@Api(value = "/pets", tags = "Pets", description = "关于pets的操作")
public class PetController {

  PetRepository petData = new PetRepository();

  @RequestMapping(value = "/{petId}", method = GET)
  @ApiOperation(
          value = "通过ID查找宠物", notes = "当ID <10时，返回宠物。ID> 10或非整数将模拟API" +
          "错误条件",
          response = Pet.class,
          authorizations = {
                  @Authorization(value = "api_key"),
                  @Authorization(value = "petstore_auth", scopes = {
                          @AuthorizationScope(scope = "write_pets", description = ""),
                          @AuthorizationScope(scope = "read_pets", description = "")
                  })})
  @ApiResponses(value = {
          @ApiResponse(code = 400, message = "提供的ID无效"),
          @ApiResponse(code = 404, message = "Pet没找到")}
  )
  public ResponseEntity<Pet> getPetById(
          @ApiParam(value = "需要提取的宠物ID", allowableValues = "range[1,5]", required = true)
          @PathVariable("petId") String petId)
          throws NotFoundException {
    Pet pet = petData.get(Long.valueOf(petId));
    if (null != pet) {
      return Responses.ok(pet);
    } else {
      throw new NotFoundException(404, "Pet not found");
    }
  }

  @RequestMapping(method = POST)
  @ApiOperation(value = "添加一个新宠物到商店")
  @ApiResponses(value = {@ApiResponse(code = 405, message = "Invalid input")})
  public ResponseEntity<String> addPet(
          @ApiParam(value = "需要添加到商店的宠物对象", required = true) @RequestBody Pet pet) {
    petData.add(pet);
    return Responses.ok("SUCCESS");
  }

  @RequestMapping(method = PUT)
  @ApiOperation(value = "更新一个已存在的宠物信息",
          authorizations = @Authorization(value = "petstore_auth", scopes = {
                  @AuthorizationScope(scope = "write_pets", description = ""),
                  @AuthorizationScope(scope = "read_pets", description = "")
          }))
  @ApiResponses(value = {@ApiResponse(code = 400, message = "无效ID"),
          @ApiResponse(code = 404, message = "宠物没找到"),
          @ApiResponse(code = 405, message = "验证错误")})
  public ResponseEntity<String> updatePet(
          @ApiParam(value = "需要添加到商店的宠物对象", required = true) @RequestBody Pet pet) {
    petData.add(pet);
    return Responses.ok("SUCCESS");
  }

  @RequestMapping(value = "/findByStatus", method = GET)
  @ApiOperation(
          value = "通过状态查询宠物",
          notes = "多个状态值可以用逗号分隔的字符串提供",
          response = Pet.class,
          responseContainer = "List",
          authorizations = @Authorization(value = "petstore_auth", scopes = {
                  @AuthorizationScope(scope = "write_pets", description = ""),
                  @AuthorizationScope(scope = "read_pets", description = "")
          }))
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid status value")})
  /** TODO: This renders parameter as
   *
   "name": "status",
   "in": "query",
   "description": "Status values that need to be considered for filter",
   "required": false,
   "type": "array",
   "items": {"type": "string"},
   "collectionFormat": "multi",
   "default": "available"
   */
  public ResponseEntity<List<Pet>> findPetsByStatus(
          @ApiParam(value = "Status values that need to be considered for filter",
                  required = true,
                  defaultValue = "available",
                  allowableValues = "available,pending,sold",
                  allowMultiple = true)
          @RequestParam("status") String status) {
    return Responses.ok(petData.findPetByStatus(status));
  }

  @RequestMapping(value = "/findByTags", method = GET)
  @ApiOperation(
          value = "通过标签查询宠物",
          notes = "多个标签可以用逗号分隔的字符串提供。 使用tag1，tag2，tag3进行测试。",
          response = Pet.class,
          responseContainer = "List",
          authorizations = @Authorization(value = "petstore_auth", scopes = {
                  @AuthorizationScope(scope = "write_pets", description = ""),
                  @AuthorizationScope(scope = "read_pets", description = "")
          }))
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid tag value")})
  @Deprecated
  /** TODO: This renders the parameter as 
  "name": "tags",
          "in": "query",
          "description": "Tags to filter by",
          "required": false,
          "type": "array",
          "items": {"type": "string"},
          "collectionFormat": "multi" */
  public ResponseEntity<List<Pet>> findPetsByTags(
          @ApiParam(
                  value = "Tags to filter by",
                  required = true,
                  allowMultiple = true)
          @RequestParam("tags") String tags) {
    return Responses.ok(petData.findPetByTags(tags));
  }

  static class PetRepository extends MapBackedRepository<Long, Pet> {
    public List<Pet> findPetByStatus(String status) {
      return where(Pets.statusIs(status));
    }

    public List<Pet> findPetByTags(String tags) {
      return where(Pets.tagsContain(tags));
    }
  }
}
