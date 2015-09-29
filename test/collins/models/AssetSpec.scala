package collins.models

import shared.PageParams
import org.specs2._
import specification._
import play.api.test.WithApplication
import play.api.test.FakeApplication

class AssetSpec extends mutable.Specification {

  "Asset Model Specification".title

  args(sequential = true)

  "The Asset Model" should {

    "Support CRUD Operations" in new WithApplication {

      val assetTag = "tumblrtag2"
      val assetStatus = Status.Incomplete.get
      val assetType = AssetType.ServerNode.get
      val newAsset = Asset(assetTag, assetStatus, assetType)

      val result = Asset.create(newAsset)
      result.id must beGreaterThan(1L)

      val maybeAsset = Asset.findByTag(assetTag)
      maybeAsset must beSome[Asset]
      val realAsset = maybeAsset.get
      Asset.update(realAsset.copy(statusId = Status.New.get.id))
      Asset.findByTag(assetTag).map { a =>
        a.status.id mustEqual (Status.New.get.id)
      }.getOrElse(failure("Couldn't find asset but expected to"))

      Asset.findByTag(assetTag).map { a =>
        Asset.delete(a) mustEqual 1
        Asset.findById(a.id) must beNone
      }.getOrElse(failure("Couldn't find asset but expected to"))
    }

//    "Support nodeclass" in new WithApplication(FakeApplication(
//      additionalConfiguration = Map(
//        "solr.enabled" -> true,
//        "solr.repopulateOnStartup" -> true))) {
//
//      def createAssetMetas(asset: Asset, metamap: Map[String, String]) = metamap
//        .map {
//          case (k, v) =>
//            AssetMetaValue.create(AssetMetaValue(asset.id, AssetMeta.findOrCreateFromName(k).id, 0, v))
//        }
//      val nodeclassTag = "test_nodeclass"
//      val nodeclassStatus = Status.Allocated.get
//      val nodeclassType = AssetType.Configuration.get
//      val nodeclassIdentifierTag = ("IS_NODECLASS" -> "true")
//      val nodeclassMetaTags = Map("FOOT1" -> "BAR", "BAZT1" -> "BAAAAZ")
//      val assetTag = "nodeclasstest"
//      val assetStatus = Status.Allocated.get
//      val assetType = AssetType.ServerNode.get
//      val similarAssetTag = "similar_asset"
//      val similarAssetData = List[(String, Status, Map[String, String])](
//        (similarAssetTag, Status.Unallocated.get, nodeclassMetaTags),
//        ("not_similar", Status.Unallocated.get, Map[String, String]()),
//        ("similar_not_unallocated", Status.Provisioned.get, nodeclassMetaTags))
//
//      val nodeclass = Asset.create(Asset(nodeclassTag, nodeclassStatus, nodeclassType))
//      val testAsset = Asset.create(Asset(assetTag, assetStatus, assetType))
//      val nodeclassMetas = createAssetMetas(nodeclass, (nodeclassMetaTags + nodeclassIdentifierTag))
//      val assetMetas = createAssetMetas(testAsset, nodeclassMetaTags)
//      testAsset.nodeClass must_== Some(nodeclass)
//
//      val assets = similarAssetData.map {
//        case (tag, status, metatags) => {
//          val asset = Asset.create(Asset(tag, status, AssetType.ServerNode.get))
//          createAssetMetas(asset, metatags)
//          asset
//        }
//      }
//      val finder = AssetFinder.empty.copy(
//        status = Status.Unallocated,
//        assetType = Some(AssetType.ServerNode.get))
//      val expected = assets.filter { _.tag == similarAssetTag }
//      val page = PageParams(0, 10, "", "tag")
//      Asset.findByTag(assetTag).map { asset =>
//        Asset.findSimilar(asset, page, finder, AssetSort.Distribution).items must_== expected
//      }.getOrElse(failure("Couldn't find asset but expected to"))
//
//    } //support nodeclass

    "Support getters/finders" in new WithApplication {

      val assetTag = "tumblrtag1"
      val assetStatus = Status.Incomplete.get
      val assetType = AssetType.ServerNode.get
      val assetId = 1

      Asset.findByTag(assetTag) must beSome[Asset]
      Asset.findByTag(assetTag).get.tag mustEqual assetTag

      val maybeAsset = Asset.findByTag(assetTag)
      maybeAsset must beSome[Asset]
      val asset = maybeAsset.get
      val attributes = asset.getAllAttributes
      attributes.ipmi must beSome.which { ipmi =>
        ipmi.dottedAddress mustEqual "10.0.0.2"
        ipmi.dottedGateway mustEqual "10.0.0.1"
      }

    } // support getters/finders
  } // Asset should

}