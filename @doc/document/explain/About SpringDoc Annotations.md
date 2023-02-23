**Annotation comparison between SpringFox and SpringDoc**

| SpringFox                                     | SpringDoc                                                    |
| --------------------------------------------- | ------------------------------------------------------------ |
| `@Api`                                        | `@Tag`                                                       |
| `@ApiIgnore`                                  | `@Parameter(hidden = true)` <br/>or `@Operation(hidden = true)` <br/>or `@Hidden` |
| `@ApiImplicitParam`                           | `@Parameter`                                                 |
| `@ApiImplicitParams`                          | `@Parameters`                                                |
| `@ApiModel`                                   | `@Schema`                                                    |
| `@ApiModelProperty(hidden = true)`            | `@Schema(accessMode = READ_ONLY)`                            |
| `@ApiModelProperty`                           | `@Schema`                                                    |
| `@ApiOperation(value = "foo", notes = "bar")` | `@Operation(summary = "foo", description = "bar")`           |
| `@ApiParam`                                   | `@Parameter`                                                 |
| `@ApiResponse(code = 404, message = "foo")`   | `@ApiResponse(responseCode="404", description="foo")`        |

