package com.example.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WeatherDao_Impl(
  __db: RoomDatabase,
) : WeatherDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFavoriteCity: EntityInsertAdapter<FavoriteCity>

  private val __insertAdapterOfWeatherCache: EntityInsertAdapter<WeatherCache>

  private val __deleteAdapterOfFavoriteCity: EntityDeleteOrUpdateAdapter<FavoriteCity>
  init {
    this.__db = __db
    this.__insertAdapterOfFavoriteCity = object : EntityInsertAdapter<FavoriteCity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `favorite_cities` (`id`,`name`,`latitude`,`longitude`,`country`,`admin1`,`addedAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteCity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindDouble(3, entity.latitude)
        statement.bindDouble(4, entity.longitude)
        val _tmpCountry: String? = entity.country
        if (_tmpCountry == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpCountry)
        }
        val _tmpAdmin1: String? = entity.admin1
        if (_tmpAdmin1 == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpAdmin1)
        }
        statement.bindLong(7, entity.addedAt)
      }
    }
    this.__insertAdapterOfWeatherCache = object : EntityInsertAdapter<WeatherCache>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `weather_cache` (`latLonKey`,`weatherJson`,`lastUpdated`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WeatherCache) {
        statement.bindText(1, entity.latLonKey)
        statement.bindText(2, entity.weatherJson)
        statement.bindLong(3, entity.lastUpdated)
      }
    }
    this.__deleteAdapterOfFavoriteCity = object : EntityDeleteOrUpdateAdapter<FavoriteCity>() {
      protected override fun createQuery(): String = "DELETE FROM `favorite_cities` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteCity) {
        statement.bindLong(1, entity.id)
      }
    }
  }

  public override suspend fun insertFavorite(city: FavoriteCity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFavoriteCity.insert(_connection, city)
  }

  public override suspend fun insertCache(cache: WeatherCache): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfWeatherCache.insert(_connection, cache)
  }

  public override suspend fun deleteFavorite(city: FavoriteCity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfFavoriteCity.handle(_connection, city)
  }

  public override fun getAllFavorites(): Flow<List<FavoriteCity>> {
    val _sql: String = "SELECT * FROM favorite_cities ORDER BY addedAt DESC"
    return createFlow(__db, false, arrayOf("favorite_cities")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfCountry: Int = getColumnIndexOrThrow(_stmt, "country")
        val _columnIndexOfAdmin1: Int = getColumnIndexOrThrow(_stmt, "admin1")
        val _columnIndexOfAddedAt: Int = getColumnIndexOrThrow(_stmt, "addedAt")
        val _result: MutableList<FavoriteCity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FavoriteCity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLatitude: Double
          _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          val _tmpLongitude: Double
          _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          val _tmpCountry: String?
          if (_stmt.isNull(_columnIndexOfCountry)) {
            _tmpCountry = null
          } else {
            _tmpCountry = _stmt.getText(_columnIndexOfCountry)
          }
          val _tmpAdmin1: String?
          if (_stmt.isNull(_columnIndexOfAdmin1)) {
            _tmpAdmin1 = null
          } else {
            _tmpAdmin1 = _stmt.getText(_columnIndexOfAdmin1)
          }
          val _tmpAddedAt: Long
          _tmpAddedAt = _stmt.getLong(_columnIndexOfAddedAt)
          _item =
              FavoriteCity(_tmpId,_tmpName,_tmpLatitude,_tmpLongitude,_tmpCountry,_tmpAdmin1,_tmpAddedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun isFavorite(cityId: Long): Boolean {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cityId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCachedWeather(key: String): WeatherCache? {
    val _sql: String = "SELECT * FROM weather_cache WHERE latLonKey = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, key)
        val _columnIndexOfLatLonKey: Int = getColumnIndexOrThrow(_stmt, "latLonKey")
        val _columnIndexOfWeatherJson: Int = getColumnIndexOrThrow(_stmt, "weatherJson")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "lastUpdated")
        val _result: WeatherCache?
        if (_stmt.step()) {
          val _tmpLatLonKey: String
          _tmpLatLonKey = _stmt.getText(_columnIndexOfLatLonKey)
          val _tmpWeatherJson: String
          _tmpWeatherJson = _stmt.getText(_columnIndexOfWeatherJson)
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _result = WeatherCache(_tmpLatLonKey,_tmpWeatherJson,_tmpLastUpdated)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCachedWeatherFlow(key: String): Flow<WeatherCache?> {
    val _sql: String = "SELECT * FROM weather_cache WHERE latLonKey = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("weather_cache")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, key)
        val _columnIndexOfLatLonKey: Int = getColumnIndexOrThrow(_stmt, "latLonKey")
        val _columnIndexOfWeatherJson: Int = getColumnIndexOrThrow(_stmt, "weatherJson")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "lastUpdated")
        val _result: WeatherCache?
        if (_stmt.step()) {
          val _tmpLatLonKey: String
          _tmpLatLonKey = _stmt.getText(_columnIndexOfLatLonKey)
          val _tmpWeatherJson: String
          _tmpWeatherJson = _stmt.getText(_columnIndexOfWeatherJson)
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _result = WeatherCache(_tmpLatLonKey,_tmpWeatherJson,_tmpLastUpdated)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCache(key: String) {
    val _sql: String = "DELETE FROM weather_cache WHERE latLonKey = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, key)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
