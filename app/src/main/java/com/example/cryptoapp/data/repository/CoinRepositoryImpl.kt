package com.example.cryptoapp.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.cryptoapp.data.database.AppDatabase
import com.example.cryptoapp.data.database.CoinInfoDbModel
import com.example.cryptoapp.data.mapper.CoinMapper
import com.example.cryptoapp.data.workers.RefreshDataWorker
import com.example.cryptoapp.domain.CoinInfo
import com.example.cryptoapp.domain.CoinRepository

class CoinRepositoryImpl(
    private val application: Application
) : CoinRepository {

    private val coinInfoDao = AppDatabase.getInstance(application).coinPriceInfoDao()
    private val mapper = CoinMapper()

    override fun getCoinInfoList(): LiveData<List<CoinInfo>> {
        val coinInfoList: LiveData<List<CoinInfoDbModel>> = coinInfoDao.getCoinInfoList()

        val mediatorLiveData = MediatorLiveData<List<CoinInfoDbModel>>()
        mediatorLiveData.addSource(coinInfoList) {
            mediatorLiveData.value = it
        }

        return mediatorLiveData.map {
            it.map {
                mapper.mapDbModelToEntity(it)
            }
        }
    }

    override fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo> {
        val coinInfoDbModel: LiveData<CoinInfoDbModel> = coinInfoDao.getCoinInfo(fromSymbol)

        val mediatorLiveData = MediatorLiveData<CoinInfoDbModel>()
        mediatorLiveData.addSource(coinInfoDbModel) {
            mediatorLiveData.value = it
        }

        return mediatorLiveData.map {
            mapper.mapDbModelToEntity(it)
        }
    }

    override fun loadData() {
        val workManager = WorkManager.getInstance(application)
        workManager.enqueueUniqueWork(
            RefreshDataWorker.NAME,
            ExistingWorkPolicy.REPLACE,
            RefreshDataWorker.makeRequest()
        )
    }
}