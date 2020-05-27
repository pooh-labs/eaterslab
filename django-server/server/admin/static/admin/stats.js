/* Graph settings */
const YEARS_TO_COVER = 8;
const MONTHS_TO_COVER = 12;
const WEEKS_TO_COVER = 4;
const DAYS_TO_COVER = 14;
const HOURS_TO_COVER = 48;

const DatasetGrouping = {
  YEAR: 'year',
  MONTH: 'month',
  WEEK: 'week',
  DAY: 'day',
  HOUR: 'hour'
}

const DatasetType = {
  OCCUPANCY: 'occupancy',
  RELATIVE_OCCUPANCY: 'relative_occupancy',
  AVERAGE_DISH_REVIEW: 'average_dish_review',
}

/* Translation messages */
const messages = {
  en: {
    message: {
      add_dataset: 'Add dataset',
      add_first_dataset: 'Add first dataset',
      add: 'Add',
      cafeteria: 'Cafeteria',
      datasets: 'Datasets',
      delete: 'Delete',
      group_by: 'Group by',
      incorect_dataset_type: 'Incorrect dataset type',
      lt_previous: '< Previous',
      next_gt: 'Next >',
      type: 'Type'
    },
    dataset_grouping: {
      'year': 'Year',
      'month': 'Month',
      'week': 'Week',
      'day': 'Day',
      'hour': 'Hour'
    },
    dataset_type: {
      'occupancy': 'Occupancy',
      'relative_occupancy': 'Relative occupancy',
      'average_dish_review': 'Average dish review'
    }
  },
  pl: {
    message: {
      add: 'Dodaj',
      add_dataset: 'Dodaj dane',
      add_first_dataset: 'Dodaj pierwszy zbiór danych',
      cafeteria: 'Stołówka',
      datasets: 'Dane',
      delete: 'Usuń',
      group_by: 'Grupuj według',
      incorect_dataset_type: 'Nieprawidłowy typ danych',
      lt_previous: '< Poprzednie',
      next_gt: 'Następne >',
      type: 'Typ'
    },
    dataset_grouping: {
      'year': 'lat',
      'month': 'miesięcy',
      'week': 'tygodni',
      'day': 'dni',
      'hour': 'godzin'
    },
    dataset_type: {
      'occupancy': 'zajętość',
      'relative_occupancy': 'względna zajętość',
      'average_dish_review': 'średnia ocena dania'
    }
  }
}

/* Date utilities */

function padWithZero(number) {
  return ('0' + number).slice(-2);
}

function monthLeadZero(date) {
  return padWithZero(1 + date.getMonth());
}

function dateYM(date) {
  const year = date.getFullYear();
  const month = monthLeadZero(date);
  return `${year}-${month}`;
}

function dateYMD(date) {
  const day = padWithZero(date.getDate());
  return `${dateYM(date)}-${day}`;
}

function dateYMDHM(date) {
  const hour = padWithZero(date.getHours());
  const minute = padWithZero(date.getMinutes());
  return `${dateYMD(date)} ${hour}:${minute}`;
}

/* Color utilities */

class Color {
  constructor(hex, r, g, b, h, s, l) {
    this.hex = hex;
    this.r = r;
    this.g = g;
    this.b = b;
    this.h = h;
    this.s = s;
    this.l = l;
  }

  rgba(alpha) {
    return `rgba(${this.r}, ${this.g}, ${this.b}, ${alpha})`;
  }

  hsla(alpha) {
    return `hsla(${this.h}, ${this.s}, ${this.l}, ${alpha})`;
  }
}

class ColorMaker {
  constructor() {
    this.colors = [
      new Color('#374EBF', 55, 78, 191, 230, 71, 75),
      new Color('#BF3A37', 191, 58, 55, 1, 71, 75),
      new Color('#45BF37', 69, 191, 55, 114, 71, 75),
      new Color('#BF378D', 191, 55, 141, 322, 71, 75),
      new Color('#BF9037', 191, 144, 55, 39, 71, 75),
    ];
    this.next_color = 0;
  }

  nextColor() {
    let result = this.colors[this.next_color];
    this.next_color = (this.next_color + 1) % this.colors.length;
    return result;
  }
};

var colorMaker = new ColorMaker();

const DatasetTableRow = Vue.component('dataset-table', {
  template: '#template-dataset-table-row',
  props: ['cafeteria', 'dataset'],
  methods: {
    emit_delete: function() {
      this.$emit('delete-dataset', {
        cafeteria_id: this.dataset.cafeteria_id,
        type: this.dataset.type
      });
    }
  }
})

const DatasetTable = Vue.component('dataset-table', {
  template: '#template-dataset-table',
  props: ['cafeterias', 'datasets'],
  components: {
    'dataset-table-row': DatasetTableRow
  },
  methods: {
    on_delete_dataset: function(key) {
      this.$emit('delete-dataset', key);
    }
  }
})

/* Chart initialization */
var chart = null;
var plots = [];
var xScale = new Plottable.Scales.Time();
var yLeftScale = new Plottable.Scales.Linear();
var yRightScale = new Plottable.Scales.Linear();
var xAxis = new Plottable.Axes.Numeric(xScale, "bottom");
var yLeftAxis = new Plottable.Axes.Numeric(yLeftScale, "left");
var yRightAxis = new Plottable.Axes.Numeric(yRightScale, "right");

yLeftScale.domainMin(0);

const reviewScaleTicks = [0, 1, 2, 3, 4, 5];
yRightScale.tickGenerator(function() {
  return reviewScaleTicks;
});
yRightScale.domain([0, 5.5]);

const i18n = new VueI18n({
  locale: LANGUAGE,
  messages,
});

const App = new Vue({
  i18n,
  data: {
    loading: true,
    popup: false,
    error: null,
    cafeterias: {},
    datasets: [],
    data_types: DatasetType,
    data_groupings: DatasetGrouping,
    group_by: DatasetGrouping.DAY,
    date_from: null,
    date_to: null,
  },
  watch: {
    group_by: function() {
      this.change_grouping();
    }
  },
  computed: {
    empty: function() {
      return this.datasets.length == 0;
    }
  },
  updated: function() {
    if (this.loading || this.error) {
      return;
    }
    this.renderChart();
  },
  methods: {
    showLoading: function(msg) {
      Object.assign(this, {
        'loading': true,
        'error': null,
        'popup': false
      });
    },
    showError: function(msg) {
      Object.assign(this, {
        'loading': false,
        'error': msg,
        'popup': false
      });
    },
    showChart: function() {
      Object.assign(this, {
        'loading': false,
        'error': null,
        'popup': false
      });
    },
    show_popup: function() {
      this.popup = true;
    },
    hide_popup: function() {
      this.popup = false;
    },
    load_dataset: function(cafeteria_id, type) {
      let app = this;
      switch (type) {
        case DatasetType.OCCUPANCY:
          return window.loadOccupancy(cafeteria_id, date_from, date_to);
        case DatasetType.RELATIVE_OCCUPANCY:
          return window.loadRelOccupancy(cafeteria_id, date_from, date_to);
        case DatasetType.AVERAGE_DISH_REVIEW:
          return window.loadAvgReview(cafeteria_id, date_from, date_to, this.group_by);
        default:
          return new Promise(function(resolve, reject) {
            reject(new Error(app.$i18n.t('message.incorect_dataset_type')));
          });
      }
    },
    add_dataset: function() {
      this.showLoading();
      
      let cafeteria_id = document.getElementById('add-dataset-cafeteria').value;
      let type = document.getElementById('add-dataset-type').value;

      let app = this;
      this.load_dataset(cafeteria_id, type)
        .then(data => {
          app.datasets.push({
            'cafeteria_id': cafeteria_id,
            'type': type,
            'data': data
          });
        })
        .then(() => app.showChart())
        .catch(error => App.showError(error.message));
    },
    delete_dataset: function(key) {
      for (let i=0; i<this.datasets.length; i++) {
        let other = this.datasets[i];
        if (other.cafeteria_id != key.cafeteria_id || other.type != key.type) {
          continue;
        }
        this.datasets.splice(i, 1);
        break;
      }
    },
    reload_datasets: function() {
      // Make copy of keys
      let new_datasets = [];
      for (let i=0; i<this.datasets.length; i++) {
        let ds = this.datasets[i];
        new_datasets.push({
          cafeteria_id: ds.cafeteria_id,
          type: ds.type
        })
      }

      // Set UI to loading
      Object.assign(this, {
        'loading': true,
        'error': null,
        'popup': false,
        'datasets': []
      });
      this.showLoading();

      // Start reloading all promises
      let loaders = [];
      for (let i=0; i<new_datasets.length; i++) {
        let ds = new_datasets[i];
        loaders.push(
          this.load_dataset(ds.cafeteria_id, ds.type)
          .then(data => {
            ds.data = data;
          })
        );
      }

      let app = this;
      Promise.all(loaders)
        .then(() => {
          Object.assign(this, {
            'loading': false,
            'error': null,
            'popup': false,
            'datasets': new_datasets
          });
        })
        .then(() => app.showChart())
        .catch(error => App.showError(error.message));
    },
    change_grouping: function() {
      var interval = [null, null];
      switch (this.group_by) {
        case DatasetGrouping.YEAR:
          interval = this.group_by_year();
          break;
        case DatasetGrouping.MONTH:
          interval = this.group_by_month();
          break;
        case DatasetGrouping.WEEK:
          interval = this.group_by_week();
          break;
        case DatasetGrouping.DAY:
          interval = this.group_by_day();
          break;
        case DatasetGrouping.HOUR:
          interval = this.group_by_hour();
          break;
      }

      this.date_from = interval[0];
      this.date_to = interval[1];
      this.reload_datasets();
    },
    prev_date_range: function() {
      this.move_date_range(-1);
    },
    next_date_range: function() {
      this.move_date_range(+1);
    },
    move_date_range: function(delta) {
      var interval = [new Date(date_from), new Date(date_to)];
      switch (this.group_by) {
        case DatasetGrouping.YEAR:
          interval[0].setFullYear(interval[0].getFullYear() + delta*YEARS_TO_COVER);
          interval[1].setFullYear(interval[1].getFullYear() + delta*YEARS_TO_COVER);
          break;
        case DatasetGrouping.MONTH:
          interval[0].setMonth(interval[0].getMonth() + delta*MONTHS_TO_COVER);
          interval[1].setMonth(interval[1].getMonth() + delta*MONTHS_TO_COVER);
          break;
        case DatasetGrouping.WEEK:
          interval[0].setDate(interval[0].getDate() + delta*WEEKS_TO_COVER*7);
          interval[1].setDate(interval[1].getDate() + delta*WEEKS_TO_COVER*7);
          break;
        case DatasetGrouping.DAY:
          interval[0].setDate(interval[0].getDate() + delta*DAYS_TO_COVER);
          interval[1].setDate(interval[1].getDate() + delta*DAYS_TO_COVER);
          break;
        case DatasetGrouping.HOUR:
          interval[0].setHours(interval[0].getHours() + delta*HOURS_TO_COVER);
          interval[1].setHours(interval[1].getHours() + delta*HOURS_TO_COVER);
          break;
      }

      date_from = interval[0];
      date_to = interval[1];
      this.reload_datasets();
    },
    group_by_year: function() {
      date_to = new Date();
      date_to.setFullYear(date_to.getFullYear() + 1);
      date_to.setMonth(0);
      date_to.setDate(1);
      date_to.setHours(0);
      date_to.setMinutes(0);
      date_to.setSeconds(0);
      date_to.setMilliseconds(0);
      date_from = new Date(date_to);
      date_from.setFullYear(date_from.getFullYear() - YEARS_TO_COVER + 1);

      return [date_from, date_to];
    },
    group_by_month: function() {
      date_to = new Date();
      date_to.setMonth(date_to.getMonth() + 1);
      date_to.setDate(1);
      date_to.setHours(0);
      date_to.setMinutes(0);
      date_to.setSeconds(0);
      date_to.setMilliseconds(0);
      date_from = new Date(date_to);
      date_from.setMonth(date_from.getMonth() - MONTHS_TO_COVER + 1);

      return [date_from, date_to];
    },
    group_by_week: function() {
      const FIRST_DAY_MONDAY = true;

      const curr_date = new Date();
      const curr_day = new Date(curr_date.getFullYear(), curr_date.getMonth(), curr_date.getDate());
      const diff = curr_day.getDate() - curr_day.getDay() + (FIRST_DAY_MONDAY ? (curr_day.getDay() == 0 ? -6:1) : 0);
      date_to = new Date(curr_day.setDate(diff + 7));
      date_from = new Date(date_to);
      date_from.setDate(date_from.getDate() - WEEKS_TO_COVER*7 + 1);

      return [date_from, date_to];
    },
    group_by_day: function() {
      date_to = new Date();
      date_to.setDate(date_to.getDate() + 1);
      date_to.setHours(0);
      date_to.setMinutes(0);
      date_to.setSeconds(0);
      date_to.setMilliseconds(0);
      date_from = new Date(date_to);
      date_from.setDate(date_from.getDate() - DAYS_TO_COVER + 1);

      return [date_from, date_to];
    },
    group_by_hour: function() {
      date_to = new Date();
      date_to.setHours(date_to.getHours() + 1);
      date_to.setMinutes(0);
      date_to.setSeconds(0);
      date_to.setMilliseconds(0);
      date_from = new Date(date_to);
      date_from.setHours(date_from.getHours() - HOURS_TO_COVER + 1);

      return [date_from, date_to];
    },
    update_header: function() {
      let from_string = null;
      let to_string = null;
      switch (this.group_by) {
        case DatasetGrouping.YEAR:
          from_string = date_from.getFullYear();
          to_string = date_to.getFullYear();
          break;
        case DatasetGrouping.MONTH:
          from_string = dateYM(date_from);
          to_string = dateYM(date_to);
          break;
        case DatasetGrouping.WEEK:
          from_string = dateYMD(date_from);
          to_string = dateYMD(date_to);
          break;
        case DatasetGrouping.DAY:
          from_string = dateYMD(date_from);
          to_string = dateYMD(date_to);
          break;
        case DatasetGrouping.HOUR:
          from_string = dateYMDHM(date_from);
          to_string = dateYMDHM(date_to);
          break;
      }

      const header_string = from_string + ' — ' + to_string;
      document.getElementById('range-name').textContent = header_string;
    },
    renderChart: function() {
      if (this.empty) {
        return;
      }

      let ranges = {
        min: null,
        max: null
      }

      // Destroy existing plots
      for (let plot of plots) {
        plot.detach();
        plot.destroy(); 
      }

      let show_guideline = false;
      plots = this.datasets.map(dataset => {
        if (dataset.data.length == 0) {
          return null;
        }

        // Get min and max
        const dates = dataset.data.map(entry => entry.x);
        const min = dates.reduce(function (pre, cur) {
          return Date.parse(pre) > Date.parse(cur) ? cur : pre;
        });
        const max = dates.reduce(function (pre, cur) {
          return Date.parse(pre) < Date.parse(cur) ? cur : pre;
        });

        if (ranges.min == null || ranges.min > min) {
          ranges.min = min;
        }
        if (ranges.max == null || ranges.max < max) {
          ranges.max = max;
        }

        const color = this.cafeterias[dataset.cafeteria_id].color;
        let plot = null;
        switch (dataset.type) {
          case DatasetType.OCCUPANCY:
            plot = new Plottable.Plots.Line()
            plot.y(function(d) { return d.y; }, yLeftScale);
            plot.attr("stroke", color.hex);
            plot.attr("stroke-width", 1);
            break;

          case DatasetType.RELATIVE_OCCUPANCY:
            plot = new Plottable.Plots.Line()
            plot.y(function(d) { return d.y; }, yLeftScale);
            plot.attr("stroke", color.hex);
            plot.attr("stroke-dasharray", "4 2");
            plot.attr("stroke-width", 1);
            show_guideline = true;
            break;

          case DatasetType.AVERAGE_DISH_REVIEW:
            plot = new Plottable.Plots.Bar()
            plot.y(function(d) { return d.y; }, yRightScale);
            plot.attr("fill", color.rgba(0.2));
            break;
        }

        plot
          .x(function(d) { return d.x; }, xScale)
          .addDataset(new Plottable.Dataset(dataset.data));
        return plot;
      }).filter(Boolean);

      // Set axis format specifier
      let format_specifier = null;
      switch (this.group_by) {
        case DatasetGrouping.YEAR:
          format_specifier = "%Y";
          break;
        case DatasetGrouping.MONTH:
          format_specifier = "%Y-%m";
          break;
        case DatasetGrouping.WEEK:
          format_specifier = "%Y-%m-%d";
          break;
        case DatasetGrouping.DAY:
          format_specifier = "%Y-%m-%d";
          break;
        case DatasetGrouping.HOUR:
          format_specifier = "%m-%d %H:%M";
          break;
      }
      xAxis.formatter(Plottable.Formatters.time(format_specifier));

      if (show_guideline) {
        var guideline = new Plottable.Components.GuideLineLayer("horizontal");
        guideline.scale(yLeftScale);
        guideline.value(1);
        plots.push(guideline);
      }

      const group = new Plottable.Components.Group(plots);
      chart = new Plottable.Components.Table([
        [yLeftAxis, group, yRightAxis],
        [null, xAxis, null]
      ]);
      chart.renderTo('#graph-contents');

      this.update_header();
    }
  },
  mounted: function() {
    this.change_grouping();

    let app = this;
    loadCafeterias()
      .then(() => app.showChart())
      .catch(error => app.showError(error.message));
  }
});

function loadCafeterias() {
  return fetch(API_URL_BASE + 'cafeterias/?fields=id,name&owner_id=' + USER_ID)
    .then(response => {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('HTTP status ' + response.status);
      }
    })
    .then(data => {
      let result = {};
      data.forEach(cafeteria => {
        cafeteria.color = colorMaker.nextColor();
        result[cafeteria.id] = cafeteria;
      });
      return result;
    })
    .then(data => {
      Vue.set(App, 'cafeterias', data);
    });
}

function makeOccupancyData(startDate, endDate) {
  const currentDate = new Date();
  startDate = startDate || new Date();
  var startYear = startDate.getFullYear();
  var startMonth = startDate.getMonth();
  var startDay = startDate.getDate();
  var toReturn = [];
  let i = 0;
  while (true) {
    const timestamp = new Date(startDate);
    timestamp.setHours(startDate.getHours() + i);
    if (timestamp >= endDate || timestamp >= currentDate) {
      break;
    }

    let people_in = Math.round(Math.max(i > 0 ? toReturn[i - 1].value.total + Math.random() * 10 - 5 : Math.random() * 5, 0));
    toReturn[i] = {
      timestamp: timestamp,
      value: {
        relative: people_in / 50,
        total: people_in
      }
    };

    i++;
  };
  return toReturn;
}

function loadOccupancy(cafeteria_id, from, to) {
  let sample_data = [
    {
      timestamp: '2020-05-20T09:37:38.593Z',
      value: {
        relative: 0.86, // 86% occupied
        total: 43       // 43 people inside
      }
    },
    {
      timestamp: '2020-05-20T09:38:38.593Z',
      value: {
        relative: 0.98,
        total: 49
      }
    },
    {
      timestamp: '2020-05-20T09:39:38.593Z',
      value: {
        relative: 0.90,
        total: 45
      }
    },
  ];

  let sample_promise = new Promise(function(resolve, reject) {
    setTimeout(() => {
      resolve(makeOccupancyData(from, to));
    }, DEBUGGING_LAG);
  });

  // TODO: Replace sample_promise with actual API call when implemented
  /*
  fetch(API_URL_BASE + 'cafeterias/' + d + '/occupancy/?from=' + from.toISOString() + '&to=' + to.toISOString())
    .then(response => {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('HTTP status ' + response.status);
      }
    })
  */
  return sample_promise
    .then(data => {
      let result = [];
      data.forEach(entry => {
        result.push({
          x: new Date(entry.timestamp),
          y: entry.value.total
        });
      });
      return result;
    });
}

function loadRelOccupancy(cafeteria_id, from, to) {
  let sample_data = [
    {
      timestamp: '2020-05-20T09:37:38.593Z',
      value: {
        relative: 0.86, // 86% occupied
        total: 43       // 43 people inside
      }
    },
    {
      timestamp: '2020-05-20T09:38:38.593Z',
      value: {
        relative: 0.98,
        total: 49
      }
    },
    {
      timestamp: '2020-05-20T09:39:38.593Z',
      value: {
        relative: 0.90,
        total: 45
      }
    },
  ];

  let sample_promise = new Promise(function(resolve, reject) {
    setTimeout(() => {
      resolve(makeOccupancyData(from, to));
    }, DEBUGGING_LAG);
  });

  // TODO: Replace sample_promise with actual API call when implemented
  /*
  fetch(API_URL_BASE + 'cafeterias/' + d + '/occupancy/?from=' + from.toISOString() + '&to=' + to.toISOString())
    .then(response => {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('HTTP status ' + response.status);
      }
    })
  */
  return sample_promise
    .then(data => {
      let result = [];
      data.forEach(entry => {
        result.push({
          x: new Date(entry.timestamp),
          y: entry.value.relative
        });
      });
      return result;
    });
}

function makeAvgReviewData(startDate, endDate, groupBy) {
  startDate = startDate || new Date();
  var startYear = startDate.getFullYear();
  var startMonth = startDate.getMonth();
  var startDay = startDate.getDate();
  var toReturn = [];
  let i = 0;
  while (true) {
    const timestamp = new Date(startDate);

    switch (groupBy) {
      case DatasetGrouping.YEAR:
        timestamp.setFullYear(startDate.getFullYear() + i);
        break;
      case DatasetGrouping.MONTH:
        timestamp.setMonth(startDate.getMonth() + i);
        break;
      case DatasetGrouping.WEEK:
        timestamp.setDate(startDate.getDate() + 7*i);
        break;
      case DatasetGrouping.DAY:
        timestamp.setDate(startDate.getDate() + i);
        break;
      case DatasetGrouping.HOUR:
        timestamp.setHours(startDate.getHours() + i);
        break;
    }

    if (timestamp >= endDate) {
      break;
    }

    toReturn[i] = {
      timestamp: timestamp,
      value: Math.random() * 5
    };

    i++;
  };
  return toReturn;
}

function loadAvgReview(cafeteria_id, from, to, group_by) {
  let sample_data = [
    {
      timestamp: '2020-05-18T00:00:00.000Z',
      value: Math.random() * 5
    },
    {
      timestamp: '2020-05-19T00:00:00.000Z',
      value: Math.random() * 5
    },
    {
      timestamp: '2020-05-20T00:00:00.000Z',
      value: Math.random() * 5
    },
  ];

  let sample_promise = new Promise(function(resolve, reject) {
    setTimeout(() => {
      resolve(makeAvgReviewData(from, to, group_by));
    }, DEBUGGING_LAG);
  });

  // TODO: Replace sample_promise with actual API call when implemented
  /*
  fetch(API_URL_BASE + 'cafeterias/' + d + '/avg_dish_review/?from=' + from.toISOString() + '&to=' + to.toISOString() + '&group_by=' + group_by)
    .then(response => {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('HTTP status ' + response.status);
      }
    })
  */
  return sample_promise
    .then(data => {
      let result = [];
      data.forEach(entry => {
        result.push({
          x: new Date(entry.timestamp),
          y: entry.value
        });
      });
      return result;
    });
}

document.addEventListener('DOMContentLoaded', (event) => {
  App.$mount('#content-main');

  window.addEventListener('resize', function() {
    if (chart) {
      chart.redraw();
    }
  });
});
