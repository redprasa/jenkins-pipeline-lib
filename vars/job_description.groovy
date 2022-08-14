def call(Map stageParams) {
  String descriptionString = ""
  for (element in mapToList(stageParams["parameters"])) {
     descriptionString = descriptionString + element[0] + ":" + element[1] + " "
  }

  //Setting Job description
  stageParams["currentBuild"].description = descriptionString
}

// Required due to JENKINS-27421
@NonCPS
def List<List<?>> mapToList(Map map) {
  return map.collect { it ->
    [it.key, it.value]
  }
}
