package com.example.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WeatherDatabase_Impl : WeatherDatabase() {
  private val _weatherDao: Lazy<WeatherDao> = lazy {
    WeatherDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "8a49e1a141bc724ddd7aea9d9a4dcdb6", "6ad71c13ba8b2615573ec5f66d0c0905") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `favorite_cities` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `country` TEXT, `admin1` TEXT, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `weather_cache` (`latLonKey` TEXT NOT NULL, `weatherJson` TEXT NOT NULL, `lastUpdated` INTEGER NOT NULL, PRIMARY KEY(`latLonKey`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8a49e1a141bc724ddd7aea9d9a4dcdb6')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `favorite_cities`")
        connection.execSQL("DROP TABLE IF EXISTS `weather_cache`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsFavoriteCities: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFavoriteCities.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("latitude", TableInfo.Column("latitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("longitude", TableInfo.Column("longitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("country", TableInfo.Column("country", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("admin1", TableInfo.Column("admin1", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("addedAt", TableInfo.Column("addedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFavoriteCities: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFavoriteCities: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFavoriteCities: TableInfo = TableInfo("favorite_cities", _columnsFavoriteCities,
            _foreignKeysFavoriteCities, _indicesFavoriteCities)
        val _existingFavoriteCities: TableInfo = read(connection, "favorite_cities")
        if (!_infoFavoriteCities.equals(_existingFavoriteCities)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |favorite_cities(com.example.data.local.FavoriteCity).
              | Expected:
              |""".trimMargin() + _infoFavoriteCities + """
              |
              | Found:
              |""".trimMargin() + _existingFavoriteCities)
        }
        val _columnsWeatherCache: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWeatherCache.put("latLonKey", TableInfo.Column("latLonKey", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherCache.put("weatherJson", TableInfo.Column("weatherJson", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherCache.put("lastUpdated", TableInfo.Column("lastUpdated", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWeatherCache: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWeatherCache: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWeatherCache: TableInfo = TableInfo("weather_cache", _columnsWeatherCache,
            _foreignKeysWeatherCache, _indicesWeatherCache)
        val _existingWeatherCache: TableInfo = read(connection, "weather_cache")
        if (!_infoWeatherCache.equals(_existingWeatherCache)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |weather_cache(com.example.data.local.WeatherCache).
              | Expected:
              |""".trimMargin() + _infoWeatherCache + """
              |
              | Found:
              |""".trimMargin() + _existingWeatherCache)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "favorite_cities",
        "weather_cache")
  }

  public override fun clearAllTables() {
    super.performClear(false, "favorite_cities", "weather_cache")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(WeatherDao::class, WeatherDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun weatherDao(): WeatherDao = _weatherDao.value
}
