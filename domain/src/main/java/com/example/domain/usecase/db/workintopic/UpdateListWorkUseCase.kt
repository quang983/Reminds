package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class UpdateListWorkUseCase @Inject constructor(private val workFromTopicRepository: WorkFromTopicRepository) :
    BaseUseCase<UpdateListWorkUseCase.Param, Unit> {
    class Param(val works: List<WorkDataEntity>)

    override suspend fun invoke(params: Param) {
        /*  val list = params.works.map { it ->
              WorkDataEntity(it.id, it.name, it.groupId,
                  it.listContent.filter { it.name.isNotBlank() }
                      .map {
                          ContentDataEntity(
                              it.id, it.name,
                              it.idOwnerWork, false
                          )
                      }
                          as MutableList<ContentDataEntity>,
                  it.listContentDone.filter { it.name.isNotBlank() }
                      .map {
                          ContentDataEntity(
                              it.id, it.name,
                              it.idOwnerWork, false
                          )
                      }
                          as MutableList<ContentDataEntity>)
          }*/
        workFromTopicRepository.updateDatas(params.works)
    }
}