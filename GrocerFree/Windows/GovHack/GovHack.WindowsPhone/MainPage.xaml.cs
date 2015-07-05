using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using Windows.Devices.Geolocation;
using Windows.Storage;
using Windows.Storage.Streams;
using Windows.UI.Xaml.Controls.Maps;
using Windows.UI.Xaml.Shapes;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace GovHack
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        LocationsController locationsController = new LocationsController();
        public MainPage()
        {
            this.InitializeComponent();

            this.NavigationCacheMode = NavigationCacheMode.Required;
        }

        /// <summary>
        /// Invoked when this page is about to be displayed in a Frame.
        /// </summary>
        /// <param name="e">Event data that describes how this page was reached.
        /// This parameter is typically used to configure the page.</param>
        protected override async void OnNavigatedTo(NavigationEventArgs e)
        {
            // TODO: Prepare page for display here.

            // TODO: If your application contains multiple pages, ensure that you are
            // handling the hardware Back button by registering for the
            // Windows.Phone.UI.Input.HardwareButtons.BackPressed event.
            // If you are using the NavigationHelper provided by some templates,
            // this event is handled for you.

            Geolocator geolocator = new Geolocator();
            var geo = await geolocator.GetGeopositionAsync();
            MapControl1.ZoomLevel = 16;
            MapControl1.Center = new Geopoint(new BasicGeoposition()
            {
                Latitude = geo.Coordinate.Latitude,
                Longitude = geo.Coordinate.Longitude
            });


            List<Locations> locations = await locationsController.GetLocations();

            PopulateMap(locations);
            AddMapIcon(geo);
        }

        private void PopulateMap(List<Locations> locations)
        {
            foreach (var location in locations)
            {
                AddMapIcon(location.Latitude, location.Longitude, location.Produce);
            }
        }

        private async void AddMapIcon(Geoposition geo)
        {
            var uri = new Uri("ms-appx:///Assets/Fruit/apple.png");
            var file = await Windows.Storage.StorageFile.GetFileFromApplicationUriAsync(uri);

            MapIcon MapIcon1 = new MapIcon();
           
            MapIcon1.Image = file;
            MapIcon1.Location = new Geopoint(new BasicGeoposition()
            {
                Latitude = geo.Coordinate.Latitude,
                Longitude = geo.Coordinate.Longitude
            });
            MapIcon1.NormalizedAnchorPoint = new Point(0.5, 1.0);
            MapIcon1.Title = "Space Needle";
            MapControl1.MapElements.Add(MapIcon1);
        }

        private void AddMapIcon(double latitude, double longitude, string title)
        {
            MapIcon MapIcon1 = new MapIcon();
            //Rectangle rectangle = new Rectangle();
            //rectangle.Width = 10;
            //rectangle.Height = 10;

            MapIcon1.Location = new Geopoint(new BasicGeoposition()
            {
                Latitude = latitude,
                Longitude = longitude
            });
            MapIcon1.NormalizedAnchorPoint = new Point(0.5, 1.0);
            MapIcon1.Title = title;
            MapControl1.MapElements.Add(MapIcon1);
        }


        private void AddMapIcon()
        {
            MapIcon MapIcon1 = new MapIcon();
            MapIcon1.Location = new Geopoint(new BasicGeoposition()
            {
                Latitude = 47.620,
                Longitude = -122.349
            });
            MapIcon1.NormalizedAnchorPoint = new Point(0.5, 1.0);
            MapIcon1.Title = "Space Needle";
            MapControl1.MapElements.Add(MapIcon1);
        }

    }
}
