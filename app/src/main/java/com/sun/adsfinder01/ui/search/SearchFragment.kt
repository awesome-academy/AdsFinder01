package com.sun.adsfinder01.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.sun.adsfinder01.R
import com.sun.adsfinder01.data.model.ApiResponse
import com.sun.adsfinder01.data.model.NetworkStatus.ERROR
import com.sun.adsfinder01.data.model.NetworkStatus.SUCCESS
import com.sun.adsfinder01.data.model.Place
import com.sun.adsfinder01.data.model.Seeker
import com.sun.adsfinder01.data.model.User
import com.sun.adsfinder01.util.Constants
import com.sun.adsfinder01.util.ContextExtension.showMessage
import kotlinx.android.synthetic.main.fragment_search.buttonSearch
import kotlinx.android.synthetic.main.fragment_search.checkboxPWallType1
import kotlinx.android.synthetic.main.fragment_search.checkboxPWallType2
import kotlinx.android.synthetic.main.fragment_search.checkboxPWallType3
import kotlinx.android.synthetic.main.fragment_search.checkboxPosterType1
import kotlinx.android.synthetic.main.fragment_search.checkboxPosterType2
import kotlinx.android.synthetic.main.fragment_search.checkboxPosterType3
import kotlinx.android.synthetic.main.fragment_search.imagePrevious
import kotlinx.android.synthetic.main.fragment_search.relativeSearch
import kotlinx.android.synthetic.main.fragment_search.seekBarHeight
import kotlinx.android.synthetic.main.fragment_search.seekBarPrice
import kotlinx.android.synthetic.main.fragment_search.seekBarWidth
import kotlinx.android.synthetic.main.fragment_search.textHeightChoose
import kotlinx.android.synthetic.main.fragment_search.textPriceChoose
import kotlinx.android.synthetic.main.fragment_search.textWidthChoose
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(), OnClickListener, OnSeekBarChangeListener, OnCheckedChangeListener {

    private val user by lazy { arguments?.getParcelable(Constants.ARGUMENT_USER) as User }

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doObserve()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        view.setOnClickListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imagePrevious -> activity?.supportFragmentManager?.popBackStack()
            R.id.buttonSearch -> search()
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.seekBarWidth -> textWidthChoose.text = progress.toString()
            R.id.seekBarHeight -> textHeightChoose.text = progress.toString()
            R.id.seekBarPrice -> showPriceSearchLimit(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
    }

    private fun initComponents() {
        relativeSearch.setOnClickListener(this)
        imagePrevious.setOnClickListener(this)
        buttonSearch.setOnClickListener(this)
        seekBarWidth.setOnSeekBarChangeListener(this)
        seekBarHeight.setOnSeekBarChangeListener(this)
        seekBarPrice.setOnSeekBarChangeListener(this)
    }

    private fun doObserve() {
        viewModel.places.observe(this, Observer { response ->
            handleResponse(response)
        })
    }

    private fun showPriceSearchLimit(price: Int) {
        val priceSearch = StringBuilder()
        priceSearch.append(price)
        priceSearch.append(resources.getString(R.string.place_unit))
        textPriceChoose.text = priceSearch.toString()
    }

    private fun filterPosterTypeId(): String {
        val posterTypes = StringBuilder()

        if (checkboxPosterType1.isChecked) {
            posterTypes.append(POSTER_TYPE_1)
        }
        if (checkboxPosterType2.isChecked) {
            posterTypes.append(",")
            posterTypes.append(POSTER_TYPE_2)
        }
        if (checkboxPosterType3.isChecked) {
            posterTypes.append(",")
            posterTypes.append(POSTER_TYPE_3)
        }
        return posterTypes.toString()
    }

    private fun filterWallTypeId(): String {
        val wallTypes = StringBuilder()

        if (checkboxPWallType1.isChecked) {
            wallTypes.append(POSTER_TYPE_1)
        }

        if (checkboxPWallType2.isChecked) {
            wallTypes.append(",")
            wallTypes.append(POSTER_TYPE_2)
        }

        if (checkboxPWallType3.isChecked) {
            wallTypes.append(",")
            wallTypes.append(POSTER_TYPE_3)
        }
        return wallTypes.toString()
    }

    private fun search() {
        val posterTypes = filterPosterTypeId()
        val wallTypes = filterWallTypeId()
        val widthLimit = seekBarWidth.progress
        val heightLimit = seekBarHeight.progress
        val priceLimit = seekBarPrice.progress

        val seeker = Seeker(
            posterTypes,
            wallTypes,
            placeWidth = widthLimit,
            placeHeight = heightLimit,
            priceLimit = priceLimit
        )

        viewModel.findPlace(user.id, seeker)
    }

    private fun handleResponse(response: ApiResponse<List<Place>>) {
        when (response.status) {
            SUCCESS -> showResult(response.data)
            ERROR -> context?.showMessage(response.message)
        }
    }

    private fun showResult(places: List<Place>?) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.drawer_layout, SearchResultFragment.newInstance(user, places))
            ?.addToBackStack("")
            ?.commit()
    }

    companion object {

        private const val AUTOCOMPLETE_REQUEST_CODE = 1
        private const val COUNTRY_CODE = "VN"

        private const val POSTER_TYPE_1 = 10000
        private const val POSTER_TYPE_2 = 10001
        private const val POSTER_TYPE_3 = 10002

        fun newInstance(user: User?) = SearchFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.ARGUMENT_USER, user)
            }
        }
    }
}
