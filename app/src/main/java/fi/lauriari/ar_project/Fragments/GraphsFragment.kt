package fi.lauriari.ar_project.Fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import fi.lauriari.ar_project.viewmodels.MapDetailsViewModel
import fi.lauriari.ar_project.R
import java.text.Format
import java.text.SimpleDateFormat


class GraphsFragment : Fragment() {
    private val mMapDetailsViewModel: MapDetailsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_graphs, container, false)

        val allMapDetails = mMapDetailsViewModel.getAllMapDetails()

        val graph = view.findViewById<GraphView>(R.id.graph)

        val emeralds: List<DataPoint> = allMapDetails.map {
            DataPoint(it.time.toDouble(), it.collectedEmeralds.toDouble())
        }

        val rubies: List<DataPoint> = allMapDetails.map {
            DataPoint(it.time.toDouble(), it.collectedRubies.toDouble())
        }

        val sapphires: List<DataPoint> = allMapDetails.map {
            DataPoint(it.time.toDouble(), it.collectedSapphires.toDouble())
        }
        Log.d("sapphires", sapphires.toString())

        val topazes: List<DataPoint> = allMapDetails.map {
            DataPoint(it.time.toDouble(), it.collectedTopazes.toDouble())
        }

        graph.title = "Collected gems"
        graph.viewport.isScalable = true
        graph.viewport.setMinX(emeralds[0].x - 86400000 - 30000000)
        graph.viewport.setMaxX(emeralds[emeralds.lastIndex].x + 86400000 + 150000000)
        graph.gridLabelRenderer.numHorizontalLabels = allMapDetails.size
        Log.d("emeraldsd", emeralds[0].x.toString())

        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                if (isValueX) {
                    val formatter: Format = SimpleDateFormat("dd/MM\nyyyy")
                    return formatter.format(value)
                }
                return super.formatLabel(value, isValueX)
            }
        }


        val emeraldsBar = BarGraphSeries(arrayOf<DataPoint>())
        emeralds.forEach {
            emeraldsBar.appendData(DataPoint(it.x, it.y), true, emeralds.size)
        }
        emeraldsBar.color = Color.GREEN

        val rubiesBar = BarGraphSeries(arrayOf<DataPoint>())
        rubies.forEach {
            rubiesBar.appendData(DataPoint(it.x, it.y), true, emeralds.size)
        }
        rubiesBar.color = Color.RED

        val sapphiresBar = BarGraphSeries(arrayOf<DataPoint>())
        sapphires.forEach {
            sapphiresBar.appendData(DataPoint(it.x, it.y), true, emeralds.size)
        }
        sapphiresBar.color = Color.BLUE

        val topazesBar = BarGraphSeries(arrayOf<DataPoint>())
        topazes.forEach {
            topazesBar.appendData(DataPoint(it.x, it.y), true, emeralds.size)
        }
        topazesBar.color = Color.YELLOW

        graph.addSeries(emeraldsBar)
        graph.addSeries(rubiesBar)
        graph.addSeries(topazesBar)
        graph.addSeries(sapphiresBar)


        return view
    }
}